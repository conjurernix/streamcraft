(ns streamcraft.observability-mulog.core
  (:require [com.brunobonacci.mulog :as mu]
            [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.observability :as observability]))

(defmacro -log [event args]
  `(mu/log ~event ~@args))

(defrecord ObservabilityMulog
  [config publishers ctx]

  component/Lifecycle
  (start [this]
    (let [{:keys [publishers ctx]} config]
      (doseq [publisher publishers]
        (mu/start-publisher! publisher))
      (-> this
          (assoc :publishers publishers)
          (assoc :ctx ctx))))

  (stop [this]
    (-> this
        (assoc :config nil)
        (assoc :publishers nil)
        (assoc :ctx nil)))

  observability/ILogging
  (log! [_ event args]
    (-log event (flatten (seq args))))

  (info! [this event args]
    (observability/log! this event (assoc args :level :info)))

  (warn! [this event args]
    (observability/log! this event (assoc args :level :warn)))

  (error! [this event args]
    (observability/log! this event (assoc args :level :error)))

  (debug! [this event args]
    (observability/log! this event (assoc args :level :debug))))


(defn make-observability []
  (map->ObservabilityMulog {}))
