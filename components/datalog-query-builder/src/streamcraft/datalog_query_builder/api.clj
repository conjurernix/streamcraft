(ns streamcraft.datalog-query-builder.api
  (:require [streamcraft.datalog-query-builder.core :as core]))

(defn build-query
  "Given a map of where clauses (each key is a property and each value is a value to match)
  build a query that can be used with datalog."
  [where entity-id-key]
  (core/build-query where entity-id-key))
