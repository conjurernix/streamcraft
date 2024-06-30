(ns streamcraft.protocols.api.schema)

(defprotocol IEntityPersistenceSchemaTransformer
  (persistence-schema [this entity-schema]
    "Transforms the entity schema to a persistence schema."))
