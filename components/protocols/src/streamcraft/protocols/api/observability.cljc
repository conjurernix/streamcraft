(ns streamcraft.protocols.api.observability)

(defprotocol ILogging
  (log! [this event data]
    "Logs an event with the given data.
     - data: A map containing the event's data, along with other properties like `level`")
  (info! [this event data]
    "Logs an event with level info")
  (debug! [this event data]
    "Logs an event with level debug.")
  (warn! [this event data]
    "Logs an event with level warn")
  (error! [this event data]
    "Logs an event with level error"))
