(ns streamcraft.protocols.api.transformer.schema)

(defprotocol ISchemaTransformer
  (transform  [this schema]
    "Transforms a schema to another value (potentially another schema)"))

(defprotocol ISchemaValueTransformer
  (transform-value [this schema value]
    "Transforms a value according to a schema"))
