(ns streamcraft.protocols.api.transformer.schema)

(defprotocol ISchemaTransformer
  (transform  [this schema]
    "Transforms a schema to another value (potentially another schema)"))
