(ns streamcraft.domain.core
  (:require [streamcraft.protocols.api.entity-manager :as-alias em]))

(def schemas
  {:user [:map {::em/name "User"}
          [:first-name :string]
          [:last-name :string]]})
