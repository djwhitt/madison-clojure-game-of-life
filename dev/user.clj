(ns user
  (:require [cider.nrepl :as cider]
            [clojure.main :as clj-main]
            [hyperfiddle.rcf]
            [nrepl.server :as nrepl]
            [portal.api :as portal]
            [game-of-life.core]
            [rebel-readline.clojure.line-reader :as rr-clr]
            [rebel-readline.clojure.main :as rr-cm]
            [rebel-readline.clojure.service.local :as rr-csl]
            [rebel-readline.core :as rr]
            [taoensso.timbre :as log]))

(hyperfiddle.rcf/enable!)

(defn repl
  "Start a development REPL, intended to be invoked from ./scripts/repl"
  [{:keys [portal]}]

  (log/info "Starting nREPL server...")
  (let [{:keys [port] :as _server} (nrepl/start-server :handler cider/cider-nrepl-handler)]
    (log/infof "nREPL server started on port %d." port)
    (log/info "Writing port to .nrepl-port...")
    (spit ".nrepl-port" port))

  (when portal
    (log/info "Opening portal...")
    (portal/open)
    (add-tap #'portal/submit))

  (log/info "Starting interactive REPL...")
  (rr/with-line-reader
    (rr-clr/create (rr-csl/create))
    (clj-main/repl
     :prompt (fn [])
     :read (rr-cm/create-repl-read)))

  (log/info "Shutting down...")

  (when portal
    (log/info "Closing portal...")
    (portal/close))

  (shutdown-agents)
  (System/exit 0))
