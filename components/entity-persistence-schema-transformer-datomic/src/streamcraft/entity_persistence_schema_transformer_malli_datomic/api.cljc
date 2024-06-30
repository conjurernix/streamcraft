(ns streamcraft.entity-persistence-schema-transformer-malli-datomic.api
  (:require [streamcraft.entity-persistence-schema-transformer-malli-datomic.core :as core]))

(defn make-entity-persistence-schema-transformer []
  (core/make-entity-persistence-schema-transformer))
