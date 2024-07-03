(ns streamcraft.protocols.api.schema-transformer.persistence)

(defprotocol IPersistenceSchemaTransformer
  (transform [this entity-schema]
    "Transforms the entity schema to a persistence schema."))
