(ns advertisers.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::loading?
 (fn [db]
   (:loading?  db)))

(re-frame/reg-sub
 ::advertisers
 (fn [db]
   (:advertisers db)))

(re-frame/reg-sub
 ::error-state?
 (fn [db]
   (:error-state? db)))
