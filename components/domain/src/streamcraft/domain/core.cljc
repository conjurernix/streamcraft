(ns streamcraft.domain.core
  (:require [streamcraft.entity.api :as-alias entity]))

(def schemas
  {:user [:map {::entity/name :user}
          [:first-name :string]
          [:last-name :string]]})
