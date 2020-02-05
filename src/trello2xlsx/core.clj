(ns trello2xlsx.core
  (:require [clojure.data.json :as json]
            [dk.ative.docjure.spreadsheet :as sheet])  
  (:gen-class))

(defn- bad-json []
  (binding [*out* *err*]
    (println "Cannot parse JSON"))
  (System/exit 2))

(defn- process-card [data card]
  (if-let [name (get card "name")]
    (let [shortUrl (get card "shortUrl")
          desc (get card "desc")]
      [name shortUrl desc])
    (bad-json)))

(defn- process-list [data l]
  (if-let [name (get l "name")]
    [name 
     (let [id (get l "id")
           cards (get data "cards")]
       (vec (map #(process-card data %)
                 (filter #(= (get % "idList") id) cards))))]
    (bad-json)))
  
(defn- process-json [data out-file]
  (if-let [lists (get data "lists")]
    (let [wb-data (apply concat (map #(process-list data %) lists))]
      (let [wb (apply sheet/create-workbook wb-data)]
        (sheet/save-workbook! out-file wb)))
    (bad-json)))

(defn -main [& args]
  (println "trello2xlsx, (c) Boris Tobotras, boris@xtalk.msk.su, 2020")
  (when-not (= (count args) 2)
    (println "Usage: trello2xlsx source.json destination.xlsx")
    (System/exit 1))
  (with-open [input (clojure.java.io/reader (first args))]
    (when-let [data (json/read input)]
      (process-json data (second args)))))
