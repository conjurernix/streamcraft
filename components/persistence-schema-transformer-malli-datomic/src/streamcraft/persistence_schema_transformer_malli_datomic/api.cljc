(ns streamcraft.persistence-schema-transformer-malli-datomic.api
  (:require [streamcraft.persistence-schema-transformer-malli-datomic.core :as core]))

(defn make-persistence-schema-transformer []
  (core/make-persistence-schema-transformer))
