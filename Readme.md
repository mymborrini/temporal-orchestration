# Temporal Orchestration

Temporal is used to make services resilient reliable and easy to manage. It is
a powerful workflow engine that takes care of all the tricky parts, like
retries, timeouts and failure recovery.

## Use Case: Trip Booking System

At high level you would probably have these basic features like 

 - book a flight
 - Reserve a hotel
 - Schedule a cab

To design this, you can create 3 different microservices and then build an orchestration trip microservice 
to coordinate between them 

![img.png](images/trip_booking_use_case.png)

But what happens if the hotel service is down? Now things get tricky. Technically we need to add retry logic 
even rollback the previous successful step like cancel the flight if hotel booking fail. All this logic need
to be inside our code and even in every microservices. This is definitely a not reliable solution. How do we handle
this kind of scenario? That's where temporal comes in. Temporal handle

 - Failure recovery
 - State management
 - Orchestration across services

With the following features:

 - Auto retries
 - Timeout handling
 - Long-running workflows
 - Failure Recovery
 - State Management

### Launch temporal

Go to the folder temporal-tls and follow the Readme.md there to check the tls options. Then run

    docker compose up -d

### Using Temporal

In temporal terms all of this:

  - book a flight
  - Reserve a hotel
  - Schedule a cab

Are called *activities* and who trigger the activities is the *workflow* (The trip orchestration in our previous example)

 1. So our spring boot application will be the *Workflow client* 
 2. it connect to the Temporal Server which is the brain of the workflows. 
 3. The Temporal server contact the history service which writes workflow metadata and history events
 4. The Matching events create the task queue with workflow and activities
 5. Then the worker are the ones responsible to execute the tasks and then send back the response to the temporal server
 6. If something goes wrong temporal will Retry and Rollback automatically

![img.png](images/temporal_uml.png)

At the beginning of the application there are two new logs

    start: MultiThreadedPoller{name=Workflow Poller taskQueue="TRAVEL_TASK_QUEUE", namespace="default", identity=46252@CH-ADN-F3R6JR3}
    start: MultiThreadedPoller{name=Activity Poller taskQueue="TRAVEL_TASK_QUEUE", namespace="default", identity=46252@CH-ADN-F3R6JR3}

✅ What do these mean?
These two lines indicate that the Temporal Worker has started two separate pollers:
1. Workflow Poller – listens for new workflows to execute
2. Activity Poller – listens for new activities to execute

Both are using the same task queue:

- taskQueue="TRAVEL_TASK_QUEUE" → this is the queue where the workflow client sends tasks
- namespace="default" → the Temporal namespace (you can have multiple)
- identity=46252@CH-ADN-F3R6JR3 → identifies the worker process/machine (hostname + PID)


🧠 What is a "poller"?
A poller is a thread that periodically polls the Temporal server to check:

- ❓ "Are there any workflows to start?" → Workflow Poller
- ❓ "Are there any activities to run?" → Activity Poller

If there is work to do, the worker picks it up and processes it.



### Failure Recovery

In case of failure we have to be sure that everything comes back in a consistent state one of the best way to do it is using *Saga*.

🧠 What is a Saga in Temporal?
A Saga is a pattern used to coordinate a sequence of distributed operations in such a way that, if something fails midway, you can undo or compensate for what has already been done.
>> In Temporal, a Saga is a sequence of activities, where each step can have an associated compensation (manual rollback).


🔁 Concrete example (travel booking workflow):
1. ✈️ bookFlight()
2. 🏨 bookHotel()
3. 🚗 arrangeTransport()

If arrangeTransport() fails, you want to compensate:

- Cancel the hotel
- Cancel the flight

✅ Temporal SDK has built-in support for Sagas

You can use the Saga class from the Temporal Java SDK. Here's how it works:

    import io.temporal.workflow.saga.Saga;

    @Override
    public void bookTrip(TravelRequest req) {
        Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(false).build();
        Saga saga = new Saga(sagaOptions);

        try {
            // 1. Book flight
            activities.bookFlight(req);
            saga.addCompensation(() -> activities.cancelFlight(req));

            // 2. Book hotel
            activities.bookHotel(req);
            saga.addCompensation(() -> activities.cancelHotel(req));

            // 3. Arrange transport (this might fail)
            activities.arrangeTransport(req);

        } catch (Exception e) {
            // Something failed → trigger rollback
            saga.compensate();
            throw e;
        }
    }

🔄 What does saga.addCompensation(...) do?
It tells Temporal:
>> "If we reach this point but something fails afterward, run this compensation function to roll back."

So it doesn't undo immediately, but registers rollback logic to use in case of downstream failure.

⚙️ Saga.Options

- parallelCompensation = false: run compensations sequentially in reverse order (like an undo stack).
- true: run compensations in parallel (useful if the steps are independent).

🧪 What happens on failure?

- Temporal persists the workflow state (durable and replayable).
- When an error is thrown, and you call saga.compensate(), Temporal executes the compensation activities.
- Compensations are treated like normal activities: retryable, trackable, and safe.

🛡️ Benefits of using the Saga pattern in Temporal

- 💾 Persistent workflow state: can resume after restart/crash
- 💥 Structured and safe error handling
- 🔁 Declarative and automatic rollback logic
- 📊 Full traceability and observability via the Temporal UI


### Useful commands for postgres

- Connection: `psql -h localhost -p 45001 -U temporal`
- List of databases: `\list`
- Connect to a database: `\connect temporal`
- List of schemas inside the database: `\dn+`

*Remember that in PostgresSQL, schemas are namespaces within a database that contain objects such as tables, views, functions, etc. Setting the search path determines in which order PostgreSQL will look for objects when they are referenced without a qualified schema name.*

In PostgresSQL, you don't "connect" directly to a schema as you do with a database. Instead, you set the current search path. Here's how to do it: 
- Setting the default search path: `SET search_path TO schema_name;`  `SHOW search_path;`
- To see tables in a specific schema: `\dt schema_name.*`
- To see view in a specific schema: `\dv schema_name.*`
- To see function in a specific schema: `\df schema_name.*`

In fact, you can join tables between different schemas:

    SELECT * FROM schema1.table1
    JOIN schema2.table2 ON schema1.table1.id = schema2.table2.id;