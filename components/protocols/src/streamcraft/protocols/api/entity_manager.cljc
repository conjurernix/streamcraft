(ns streamcraft.protocols.api.entity-manager
  (:refer-clojure :exclude [name]))

(defprotocol EntityManager
  (get-registry [this]
    "Returns the registry.")
  (get-entities [this]
    "Returns the entities in the registry as a map.")
  (merge-registry [this registry]
    "Returns a new registry with the schemas merged.")
  (validate [this schema data]
    "Validates the data against the schema.")
  (generate [this schema]
    "Generates a random value based on the schema.")
  (name [this schema]
    "Returns the entity name of an entity schema.")
  (properties [this schema]
    "Returns the properties of the schema.")
  (of-type [this schema]
    "Returns the underlying schema in parametric schemas or self in non-parametric schemas.")
  (cardinality [this schema]
    "Returns either :one or :many depending on the cardinality of the schema.")
  (entity? [_this schema]
    "Returns true if the schema is an entity schema.")
  (entity-id-key [this schema]
    "Returns the id key of an entity schema.")
  (entity-id [this schema value]
    "Returns the id of a value representing an entity.")
  (select-entity-keys [this schema entity]
    "Returns entity with only the keys that are present in the schema.")
  (optional-keys [this schema] [this schema keys]
    "Returns a new schema with keys marked as optional."))
