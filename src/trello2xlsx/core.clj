(ns trello2xlsx.core
  (:require [clojure.data.json :as json]
            [dk.ative.docjure.spreadsheet :as sheet])  
  (:gen-class))

(defn- log [s]
  (binding [*out* *err*]
    (println "LOG:" s)))

(defn- bad-json []
  (log "Cannot parse JSON")
  (System/exit 2))

(defn- process-card [card]
  (if-let [name (get card "name")]
    [name (get card "shortUrl") (get card "desc")]
    (bad-json)))

(defn- eligible-card? [listId card]
  (and (= (get card "idList") listId)
       (not (get card "closed"))))

(defn- process-list [data l]
  (if-let [name (get l "name")]
    [name 
     (let [id (get l "id")
           eligible-cards (filter #(eligible-card? id %)
                                  (get data "cards"))]
       (mapv process-card eligible-cards))]
    (bad-json)))
  
(defn- process-json [data out-file]
  (if-let [lists (get data "lists")]
    (sheet/save-workbook!
     out-file
     (apply sheet/create-workbook
            (apply concat
                   (map #(process-list data %)
                        lists))))
    (bad-json)))

(defn -main [& args]
  (println "trello2xlsx, (c) Boris Tobotras, boris@xtalk.msk.su, 2020")
  
  (when-not (= (count args) 2)
    (println "Usage: trello2xlsx source.json destination.xlsx")
    (System/exit 1))
  
  (with-open [input (clojure.java.io/reader (first args))]
    (if-let [data (json/read input)]
      (process-json data (second args))
      (bad-json))))
