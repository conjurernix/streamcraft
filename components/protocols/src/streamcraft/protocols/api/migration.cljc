(ns streamcraft.protocols.api.migration)

(defprotocol IMigration
  (gen-migration [this]
    "Generate the migration."))

(defprotocol IRollback
  (gen-rollback [this]
    "Generate the rollback."))
