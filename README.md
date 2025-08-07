# world info dashboards
![](./docs/world-info-dashboards.png)


- A distributed-system for World information retrieval, processing and management (weather, clock, geolocation, local information, conversion, etc.).
>
- Java web REST API using Spring Authorization Server leveraging Ory Hydra for OAuth 2.0 and OpenID Connect. Hazelcast for distributed caching and in-memory data grid.
>
- JSON RPC Protocol using RabbitMQ asynchronous queues for RPC communication.
>
- Golang RPC client stub (RabbitMQ publisher) / Golang fuego Public RESTful OpenAPI (RPC service simplified abstraction for users interface/integration).
>
- Golang RPC server skeleton worker (RabbitMQ subscriber). Memcached for workers caching.
>
- Java RPC server implementation using Spring Boot for Web Services, MongoDB for data persistence and Hazelcast for real-time in-memory data grid and distributed caching.
>
- Kotlin Android application for mobile devices. Dashboards creation, customization and management / Data transformation and visualization / Geolocation (multiple sources), maps, routes, local images / image of the day and similar features / Notifications and alerts (on data definitions and configurations).