(ns streamcraft.protocols.api.entity-registry
  (:refer-clojure :exclude [name]))

(defprotocol EntityRegistry
  (get-registry [this]
    "")
  (merge-registry [this registry])
  (validate [this schema data])
  (generate [this schema])
  (name [this schema]))
