(ns arcadia.nrepl
  (:require [arcadia.repl.client :as arcadia]
            [clojure.repl :as repl]
            [clojure.string :as string]
            (clojure.tools.nrepl [transport :as transport]
                                 [server :as server]
                                 [middleware :refer [set-descriptor!]]
                                 [misc :as misc]
                                 [cmdline :as cli]))
  (:gen-class))

;; TODO: load from file
(def filter-patterns
  "Do not evaluate expresions that match this regexes in Arcadia."
  [#"System.getProperty"
   #"reply.eval-modes.nrepl"
   #"clojure.tools.nrepl"
   #"clojure.tools.namespace.repl"
   #"proto-repl"
   #"java.io"
   #"compliment.core"])

(defn valid-expr?
  "Returns true if expr doesn't match any of the filter-patterns."
  [expr]
  (and (string? expr)
       (not (first (filter #(re-find % expr) filter-patterns)))))

(defn extract-prompt-ns
  [prompt]
  (let [arrow-index (string/index-of prompt "=> ")]
    (.substring prompt 0 arrow-index)))

(defn prepare-response
  [res]
  (let [res (string/split res #"\n")
        prompt (last res)
        prompt-ns (extract-prompt-ns prompt)
        res (drop-last 2 res)]
    {:value (last res)
     :out (string/join \newline (drop-last res))
     :ns prompt-ns
     :printed-value 1
     :status :done}))

(defn arcadia-eval
  [handler {:keys [transport] :as msg}]
  (let [code (or (:code msg) (:file msg))]
    (if (valid-expr? code)
      (do (arcadia/send (str "(do " code ")"))
          (->> (arcadia/recv)
               (prepare-response)
               (misc/response-for msg)
               (transport/send transport)))
      (handler msg))))

(defn wrap-arcadia-repl
  [handler]
  (fn [{:keys [op] :as msg}]
    (case op
      ("eval" "load-file") (arcadia-eval handler msg)
      (handler msg))))

(set-descriptor! #'wrap-arcadia-repl
                 {:requires #{"clone"}
                  :expects #{"eval" "load-file"}
                  :handles {}})

(defn -main
  [& args]
  (server/start-server
    :port (Integer/parseInt (string/trim (slurp ".nrepl-port")))
    :handler (server/default-handler #'wrap-arcadia-repl))
  (arcadia/repl))

