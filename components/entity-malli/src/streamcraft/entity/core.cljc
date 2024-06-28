(ns streamcraft.entity.core
  (:require [com.stuartsierra.component :as component]
            [malli.core :as m]
            [malli.generator :as mg]
            [malli.util :as mu]
            [streamcraft.entity.api :as-alias entity]
            [streamcraft.protocols.api.entity-registry :as er]
            [taoensso.timbre :as log]))

(defrecord MalliEntityRegistry [schemas]
  component/Lifecycle
  (start [this]
    (log/info "Starting MalliEntityRegistry")
    (-> this
        (update :schemas merge (m/default-schemas) (mu/schemas))))
  (stop [this]
    (log/info "Stopping MalliEntityRegistry")
    (-> this
        (assoc :schemas nil)))
  er/EntityRegistry
  (get-registry [_] schemas)
  (merge-registry [this new-schemas]
    (-> this
        (update :schemas merge new-schemas)))
  (validate [_this schema data]
    (m/validate schema data {:registry schemas}))
  (generate [_this schema]
    (mg/generate schema {:registry schemas}))
  (name [_this schema]
    (-> schema
        (m/properties {:registry schemas})
        ::entity/name)))


(defn make-registry []
  (map->MalliEntityRegistry {}))