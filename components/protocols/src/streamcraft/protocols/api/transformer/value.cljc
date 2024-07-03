(ns streamcraft.protocols.api.transformer.value)

(defprotocol IValueTransformer
  (transform [this schema value]
    "Transforms a value according to a schema"))
