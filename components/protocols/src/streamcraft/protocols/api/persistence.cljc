(ns streamcraft.protocols.api.persistence)

(defprotocol IPersistence
  (db-id-key [this schema]
    "Returns the entity id key.")
  (db-id [this schema entity-id]
    "Returns the db id of the entity with entity-id.")
  (prepare [this schema data]
    "Prepares the data for persistence. This can include validation, normalization, etc.")
  (fetch [this schema id]
    "Fetch a single entity by id.")
  (search [this schema] [this schema {:keys [pull where] :as opts}]
    "Search for entities. The opts map can contain various options for filtering, sorting, etc.")
  (persist! [this schema data]
    "Persist a new entity.")
  (patch! [this schema id data]
    "Patch an existing entity. The data map should contain the fields to update. Data can be partial.")
  (delete! [this schema id]
    "Delete an entity."))

(defprotocol ITransactionalPersistence
  (persist [this schema data]
    "Prepares a new transaction operation that persists a new entity.")
  (patch [this schema id data]
    "Prepares a new transaction operation that patches an existing entity.
     The data map should contain the fields to update. Data can be partial.")
  (delete [this schema id]
    "Prepares a new transaction operation that deletes an entity.")
  (clear-txs [this]
    "Clear the accumulated transactions .")
  (transact! [this]
    "Execute the transaction."))
