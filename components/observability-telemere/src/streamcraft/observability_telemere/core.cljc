(ns streamcraft.observability-telemere.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.observability :as observability]
            [taoensso.telemere :as tel]))

(defrecord ObservabilityTelemere
  [trace-id]

  component/Lifecycle
  (start [this] this)

  (stop [this]
    (-> this
        (assoc :trace-id nil)))

  observability/ILogging
  (log! [_ message]
    (tel/log! message))

  (log! [_ level-or-opts message]
    (tel/log! level-or-opts message))

  (debug! [_ message]
    (tel/log! :debug message))

  (debug! [_ opts message]
    (tel/log! (assoc opts :level :debug) message))

  (info! [_ message]
    (tel/log! :info message))

  (info! [_ opts message]
    (tel/log! (assoc opts :level :info) message))

  (warn! [_ message]
    (tel/log! :warn message))

  (warn! [_ opts message]
    (tel/log! (assoc opts :level :warn) message))

  (error! [_ message]
    (tel/log! :error message))

  (error! [_ opts message]
    (tel/log! (assoc opts :level :error) message))

  observability/ITracing
  (start-trace [this]
    (let [trace-id (random-uuid)]
      (observability/start-trace this trace-id)))

  (start-trace [this trace-id-or-opts]
    (let [trace-id (if (map? trace-id-or-opts)
                     (:trace-id trace-id-or-opts)
                     trace-id-or-opts)]
      (assoc this :trace-id trace-id)))

  (end-trace [this]
    (dissoc this :trace-id))

  observability/ITelemetry
  (send-event! [_ event]
    (tel/event! event))

  (send-event! [_ event level-or-opts]
    (tel/event! event level-or-opts))

  (send-exception! [_ event]
    (tel/error! event))

  (send-exception! [_ event level-or-opts]
    (tel/error! event level-or-opts))

  observability/IMonitoring
  (record-metric! [_ metric-name value]
    (throw (ex-info "Not implemented" {})))

  (record-metric! [_ metric-name value opts]
    (throw (ex-info "Not implemented" {})))

  (retrieve-metric [_ metric-name]
    (throw (ex-info "Not implemented" {})))

  (increment-metric! [_ metric-name]
    (throw (ex-info "Not implemented" {})))

  (increment-metric! [_ metric-name increment-by]
    (throw (ex-info "Not implemented" {})))

  (decrement-metric! [_ metric-name]
    (throw (ex-info "Not implemented" {})))

  )


(defn make-observability []
  (map->ObservabilityTelemere {}))
