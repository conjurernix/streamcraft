(ns streamcraft.protocols.api.observability)

(defprotocol ILogging
  (log! [this event args]
    "Logs a message with the given level.
    The level can be a keyword or a map with the level and other log options.")
  (debug! [this event args]
    "Logs a debug message.")
  (info! [this event args]
    "Logs an info message.")
  (warn! [this event args]
    "Logs a warning message.")
  (error! [this event args]
    "Logs an error message."))

(defprotocol IMonitoring
  (record-metric! [this metric-name value] [this metric-name value opts]
    "Records a numeric metric with the given name and value.
     The value can be a number or a map with the value and other metric options.")
  (retrieve-metric [this metric-name]
    "Retrieves the current value of the metric with the given name.")
  (increment-metric! [this metric-name] [this metric-name increment-by]
    "Increments a numeric metric by one or by the specific value.")
  (decrement-metric! [this metric-name] [this metric-name decrement-by]
    "Decrements a numeric metric by one or by the specific value."))

(defprotocol ITelemetry
  (send-event! [this event] [this event level-or-opts]
    "Sends an event to the telemetry system.")
  (send-exception! [this exception] [this exception level-or-opts]
    "Sends an exception to the telemetry system."))

(defprotocol ITracing
  (start-trace [this] [this trace-id-or-opts]
    "Returns a new object with the trace id set.
     The trace id can be a string or a map with the trace id and other trace options.
     The trace id is used to correlate logs, metrics, and other observability data.")
  (end-trace [this]
    "Returns a new object with the trace id unset."))
