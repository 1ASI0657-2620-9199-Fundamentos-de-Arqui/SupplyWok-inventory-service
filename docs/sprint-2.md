# Sprint 2

Inventory escribe inventory.supply.minimum-stock-reached cuando se alcanza el nivel mínimo. Operations crea la alerta del restaurante.

El productor guarda el evento y la operación de negocio en la misma transacción PostgreSQL. El relay procesa filas pendientes con bloqueo y registra el envío solo después de recibir confirmación de RabbitMQ y comprobar que el mensaje se enrutó. Los fallos se reintentan con espera creciente hasta 30 segundos. Para inspeccionar el atraso, consultar el número de filas y la menor fecha occurred_at de outbox_events en el esquema del servicio.

El cuerpo JSON conserva el contrato del dominio. messageId identifica el evento. Los encabezados eventType, version y occurredAt indican la ruta, la versión 1 y el instante de creación. Los eventos de inventario y órdenes conservan restaurantId en el cuerpo. El perfil del proveedor se sincroniza por el identificador de correo existente.
