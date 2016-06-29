(ns forced.soql)

(def stanza-order
  (list
    :select
    :from
    :where
    :limit
    :offset
    :order-by))

(defn interpose-str
  [split items]
  (apply str (interpose split items)))

(defn infix-str
  [op & args]
  (interpose-str (str " " (name op) " ") args))

(defn paren-str [s] (str "(" s ")"))

(defmulti build-soql #(first %))

(defmethod build-soql :select [[_ i]]
  (str "SELECT "(interpose-str ", " (map #(if (map? %) (paren-str (soql-str %)) (name %)) i))))

(defmethod build-soql :from [[_ i]]
  (str " FROM " (name i)))

(defmethod build-soql :where [[_ i]]
  (str " WHERE " (build-soql i)))

(defmethod build-soql :default [[op & i]]
  (apply (partial infix-str op) i))

(defn soql-str
  [expression]
  (apply
    str
    (flatten
      (map
        #(when-let [i (get expression %)] (build-soql [% i]))
        stanza-order))))

(comment

  (soql-str
    {:select
     [:Id :FirstName :LastName :Email
      {:select [:Id :FirstName :LastName :Email]
       :from :Contact}]
     :from :User
     :where [:= "UserName" "someone@somewhere.net"]})
  )
