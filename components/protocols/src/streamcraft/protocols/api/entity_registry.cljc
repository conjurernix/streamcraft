(ns streamcraft.protocols.api.entity-registry)

(defprotocol EntityRegistry
  (get-registry [this])
  (get-from-registry [this key])
  (add-to-registry! [this key schema])
  (merge-registry [this registry])
  (entity? [this schema])
  (validate [this schema data])
  (generate [this schema])
  (name [this schema]))
