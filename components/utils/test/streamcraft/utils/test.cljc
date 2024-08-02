(ns streamcraft.utils.test
  (:require [com.stuartsierra.component :as component]
            [streamcraft.entity-manager.api :as entity]
            [streamcraft.persistence-datomic-pro.api :as db.datomic-pro]
            [streamcraft.migration-datomic.api :as migration.datomic]
            [streamcraft.persistence-schema-transformer-malli-datomic.api :as st.p.m.d])
  #?(:clj (:import (clojure.lang ExceptionInfo))))

; Datomic

(defn fresh-datomic-pro-persistence []
  (db.datomic-pro/make-persistence))

(defn fresh-datomic-migration []
  (migration.datomic/make-migration))

(defn fresh-malli-datomic-persistence-schema-transformer []
  (st.p.m.d/make-persistence-schema-transformer))

; Malli Entity Manager

(defn fresh-entity-manager []
  (entity/make-entity-manager))


; System

(def ^:dynamic *system* nil)

(defmacro with-system [system & body]
  `(binding [*system* (component/start-system ~system)]
     ~@body
     (component/stop-system *system*)))


(defn with-system-fixture [system]
  (fn [f]
    (with-system system
      (f))))

(defmacro catch-thrown-info [f]
  `(try
     ~f
     (catch ExceptionInfo e#
       {:message  (ex-message e#)
        :data (ex-data e#)})))