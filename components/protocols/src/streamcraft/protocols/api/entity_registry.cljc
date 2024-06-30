(ns streamcraft.protocols.api.entity-registry
  (:refer-clojure :exclude [name]))

(defprotocol EntityRegistry
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
  (entries [this schema]
    "Returns the entries of a map schema.")
  (of-type [this schema]
    "Returns the underlying schema in parametric schemas or self in non-parametric schemas.")
  (entry-key [this entry]
    "Returns the key of an entry.")
  (entry-schema [this entry]
    "Returns the schema of an entry.")
  (cardinality [this schema]
    "Returns either :one or :many depending on the cardinality of the schema.")
  (entity? [_this schema]
    "Returns true if the schema is an entity schema."))
