(ns streamcraft.protocols.api.migration)

(defprotocol IMigration
  (gen-migration [this]
    "Generate the migration.")
  ; TODO: Maybe this should be a separate protocol?
  #_(gen-rollback [this]
    "Generate the rollback."))
