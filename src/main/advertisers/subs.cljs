(ns advertisers.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::loading?
 (fn [db]
   (or (:loading?  db)
       (:stats-loading?  db))))

(re-frame/reg-sub
 ::advertisers
 (fn [db]
   (:advertisers db)))

(re-frame/reg-sub
 ::statistics
 (fn [db]
   (:statistics db)))

(re-frame/reg-sub
 ::error-state?
 (fn [db]
   (or
    (:error-state? db)
    (:stats-error-state? db))))

(re-frame/reg-sub
 ::add-statistics
 (fn [db]
   (:add-statistics db)))

(re-frame/reg-sub
 ::enriched-advertisers
 (fn [db]
   (:enriched-advertisers db)))
