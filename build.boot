(set-env!
 :source-paths  #{"src"}
 :dependencies '[[org.clojure/clojure           "1.7.0"]
                 [adzerk/boot-cljs              "0.0-3269-2" :scope "test"]
                 [org.martinklepsch/boot-garden "1.2.5-4"    :scope "test"]
                 [jeluard/boot-cssnext          "0.5.0"      :scope "test"]
                 [adzerk/boot-reload            "0.2.6"      :scope "test"]
                 [jeluard/boot-notify           "0.2.0"      :scope "test"]
                 [adzerk/bootlaces              "0.1.11"     :scope "test"]

                 [org.clojure/clojurescript "0.0-3308" :classifier "aot"]
                 [lucuma "0.5.0-SNAPSHOT"]
                 [hipo "0.5.0-SNAPSHOT"]
                 [garden "1.2.5"]])

(def dev-dependencies '[[cljsjs/document-register-element "0.4.3-0"]
                        [binaryage/devtools "0.2.2"]])

(require
 '[adzerk.boot-cljs              :refer [cljs]]
 '[org.martinklepsch.boot-garden :refer [garden]]
 '[jeluard.boot-cssnext          :refer [cssnext]]
 '[adzerk.boot-reload            :refer [reload]]
 '[jeluard.boot-notify           :refer [notify]]
 '[adzerk.bootlaces              :refer [bootlaces! build-jar]])

(def +version+ "0.5.0-SNAPSHOT")

(bootlaces! +version+)
(task-options!
  pom  {:project     'picada
        :version     +version+
        :description "A Material inspired collection of HTML elements (Custom Elements)"
        :url         "https://github.com/jeluard/picada"
        :scm         {:url "https://github.com/jeluard/picada"}
        :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask dev
  []
  (merge-env! :dependencies dev-dependencies)
  (merge-env! :source-paths #{"src-dev"})
  (comp
    (watch)
    (notify)
    (reload)
    (garden :styles-var 'picada.styles/all)
    (cssnext)
    (cljs :main "test-main" :compiler-options {:asset-path "target/out" })
    (build-jar)))
