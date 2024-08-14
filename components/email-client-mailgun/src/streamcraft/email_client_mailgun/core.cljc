(ns streamcraft.email-client-mailgun.core
  (:require [com.stuartsierra.component :as component]
            [mailgun.mail :as mail]
            [streamcraft.protocols.api.email-client :as email]
            [streamcraft.protocols.api.observability :as obs]))

(defrecord MailgunEmailClient
  [config observability]

  component/Lifecycle
  (start [this]
    (obs/info! observability :starting-component {:component MailgunEmailClient})
    this)
  (stop [this]
    (obs/info! observability :stopping-component {:component MailgunEmailClient})
    this)

  email/IEmailClient
  (send! [_ {:keys [from to subject body]}]
    (let [{:keys [credentials]} config]
      (mail/send-mail credentials {:from    from
                                   :to      to
                                   :subject subject
                                   :body    body}))))

(defn make-email-client []
  (map->MailgunEmailClient {}))
