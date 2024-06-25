(ns streamcraft.entity.core
  (:require [com.stuartsierra.component :as component]
            [malli.core :as m]
            [malli.generator :as mg]
            [malli.registry :as mr]
            [malli.util :as mu]
            [streamcraft.entity.api :as-alias entity]
            [streamcraft.protocols.api.entity-registry :as er]
            [taoensso.timbre :as log]))

(defonce registry
         (atom (merge (m/default-schemas)
                      (mu/schemas))))

(defn add-to-registry! [key schema]
  (swap! registry assoc key schema))

(defmacro defschema [name id schema]
  `(let [schema# ~schema]
     (def ~name schema#)
     (add-to-registry! ~id schema#)))

(defn as-entity [schema]
  (with-meta schema {::entity? true}))

(defmacro defentity [name id schema]
  `(defschema ~name ~id (as-entity ~schema)))

(defn init-registry! []
  (mr/set-default-registry!
    (mr/mutable-registry registry)))

; TODO - registry instead of global def can be a volatile field in a record
(defrecord MalliEntityRegistry []
  component/Lifecycle
  (start [this]
    (log/info "Starting MalliEntityRegistry")
    this)
  (stop [_]
    (log/info "Stopping MalliEntityRegistry")
    (init-registry!))
  er/EntityRegistry
  (get-registry [_] @registry)
  (get-from-registry [_ key] (get @registry key))
  (add-to-registry! [_ key schema]
    (add-to-registry! key schema))
  (merge-registry [_ new-registry]
    (swap! registry merge new-registry))
  (entity? [_this schema]
    (-> schema
        (meta)
        ::entity?))
  (validate [_this schema data]
    (m/validate schema data))
  (generate [_this schema]
    (mg/generate schema))
  (name [_this schema]
    (-> schema
        (m/properties)
        ::entity/name)))


(defn make-registry []
  (->MalliEntityRegistry))