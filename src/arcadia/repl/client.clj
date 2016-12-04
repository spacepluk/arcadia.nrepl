(ns arcadia.repl.client
  (:import (java.net InetAddress DatagramPacket DatagramSocket))
  (:refer-clojure :exclude [send]))

(def ^:dynamic *host* (InetAddress/getLocalHost))
(def ^:dynamic *port* 11211)
(defonce socket (DatagramSocket.))

(defn send [text]
  (.send socket
         (DatagramPacket. (.getBytes text)
                          (.length text)
                          *host*
                          *port*)))

(defn recv []
  (let [buffer (byte-array 2048)
        packet (DatagramPacket. buffer (count buffer))]
    (.receive socket packet)
    (String. (.getData packet) "UTF-8")))

(def preamble
  '(let [clj (clojure-version)
         unity (UnityEditorInternal.InternalEditorUtility/GetFullUnityVersion)
         mono (.Invoke (.GetMethod Mono.Runtime
                                   "GetDisplayName"
                                   (enum-or BindingFlags/NonPublic
                                            BindingFlags/Static))
                       nil
                       nil)]
     (println (str "; Arcadia nREPL bridge" \newline
                   "; Clojure" clj \newline
                   "; Unity" unity \newline
                   "; Mono" mono))
     '______________________________________))

(defn repl []
  (send (str preamble))
  (loop []
    (print (recv))
    (flush)
    (when-let [expr (read-line)]
      (send (str expr \newline))
      (recur)))
  (System/exit 0))

