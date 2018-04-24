/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_events.support;

import android.widget.TextView;

import com.sessionm.api.common.data.behavior.Behavior;
import com.sessionm.api.common.data.behavior.CompositeBehavior;
import com.sessionm.api.common.data.behavior.CountBehavior;
import com.sessionm.api.common.data.behavior.Goal;
import com.sessionm.api.common.data.behavior.Group;
import com.sessionm.api.common.data.behavior.UniqueBehavior;
import com.sessionm.api.events.data.EventsResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BehaviorList {

    private final EventsResponse _behaviorResponse;

    enum ByType { Event, Behavior }

    public BehaviorList(EventsResponse response) {
        _behaviorResponse = response;
    }

    public void show(ByType byType, TextView display) {
        if (byType == ByType.Behavior) {
            showByBehavior(display);
        }
        if (byType == ByType.Event) {
            showByEventName(display);
        }
    }

    private void showByEventName(TextView display) {
        if (_behaviorResponse == null)
            return;
        StringBuilder out = new StringBuilder();
        out.append(String.format("Points: %d\n\n", _behaviorResponse.getAvailablePoints()));

        Map<String, List<LineItem>> events = new TreeMap<>();
        for (Map.Entry<String, Behavior> behavior : _behaviorResponse.getBehaviors().entrySet()) {

            switch (behavior.getValue().getType()) {
                case Composite:
                    CompositeBehavior composite = (CompositeBehavior) behavior.getValue();
                    for (Map.Entry<String, Goal> goal : composite.getGoals().entrySet()) {
                        LineItem.addItem(events, new LineItem(behavior.getKey(), goal.getKey(), goal.getValue().getGroupID(), goal.getValue().getProgress()));
                    }
                    break;
                case Count:
                    CountBehavior count = (CountBehavior) behavior.getValue();
                    LineItem.addItem(events, new LineItem(behavior.getKey(), "", "", count));
                    break;
                case Unique:
                    UniqueBehavior unique = (UniqueBehavior) behavior.getValue();
                    break;
                default:
                    break;
            }
        }

        out.append("Events: \n\n");

        for (Map.Entry<String, List<LineItem>> event : events.entrySet()) {
            out.append("  Event: ").append(event.getKey()).append("\n");

            Collections.sort(event.getValue(), new Comparator<LineItem>() {
                @Override
                public int compare(LineItem o1, LineItem o2) {
                    String l1 = String.format("%-36s:%-20s:%-20s", o1._eventName, o1._second, o1._third);
                    String l2 = String.format("%-36s:%-20s:%-20s", o2._eventName, o2._second, o2._third);
                    return l1.compareTo(l2);
                }
            });

            for (LineItem item : event.getValue()) {
                out.append(String.format("    %2d %2d of %2d '%s'\n", item._achieved, item._currentCount, item._totalCount, item._behaviorName));
            }
        }

        //if (_behaviorResponse.getNotifications()) {
        //    out.append("Notifications: \n");
        //}
        display.setText(out.toString());
    }

    static class LineItem {
        private String _eventName;
        private final String _second;
        private final String _third;

        private int _achieved;
        private int _currentCount;
        private int _totalCount;
        private String _behaviorName;

        public LineItem(String behaviorName, String second, String third, Behavior behavior) {

            _behaviorName = behaviorName;
            _second = second;
            _third = third;

            _eventName = ((CountBehavior) behavior).getEventName();
            _achieved = ((CountBehavior) behavior).getAchieved();
            _currentCount = ((CountBehavior) behavior).getCurrentCount();
            _totalCount = ((CountBehavior) behavior).getTotalCount();
        }

        static void addItem(Map<String, List<LineItem>> events, LineItem lineItem) {
            List<LineItem> items = events.get(lineItem._eventName);
            if (items == null) {
                items = new ArrayList<LineItem>();
                events.put(lineItem._eventName, items);
            }
            items.add(lineItem);
        }

    }

    private void showByBehavior(TextView display) {
        StringBuilder out = new StringBuilder();
        outByBehaviors(out, _behaviorResponse);
        display.setText(out.toString());
    }

    private void outByBehaviors(StringBuilder out, EventsResponse response) {
        out.append(String.format("Points: %d\n\n", response.getAvailablePoints()));
        out.append("Behaviors: \n");
        for (Map.Entry<String, Behavior> behavior : response.getBehaviors().entrySet()) {
            out.append(String.format("  '%s' (%s)\n", behavior.getKey(), behavior.getValue().getType()));
            switch (behavior.getValue().getType()) {
                case Composite:
                    CompositeBehavior composite = (CompositeBehavior) behavior.getValue();
                    for (Map.Entry<String, Group> group : composite.getGroups().entrySet()) {
                        for (Map.Entry<String, Goal> goale : group.getValue().getGoals().entrySet()) {
                            Goal goal = goale.getValue();

                            if (goal.getProgress().getType() == Behavior.BehaviorType.Count) {
                                CountBehavior progress = (CountBehavior) goal.getProgress();
                                out.append(String.format("     %d %d of %d '%s'\n", progress.getAchieved(), progress.getCurrentCount(), progress.getTotalCount(), progress.getEventName()));
                            } else {
                                UniqueBehavior progress = (UniqueBehavior) goal.getProgress();
                                out.append(String.format("     %d %d of %d '%s'\n", progress.getAchieved(), progress.getCurrentCount(), progress.getTotalCount(), progress.getPoints()));
                            }
                        }
                    }
                    break;
                case Count:
                    CountBehavior count = (CountBehavior) behavior.getValue();
                    out.append(String.format("       %d of %d '%s'\n", count.getCurrentCount(), count.getTotalCount(), count.getEventName()));
                    break;
                case Unique:
                    UniqueBehavior unique = (UniqueBehavior) behavior.getValue();
                    out.append(String.format("       %d of %d '%s'\n", unique.getCurrentCount(), unique.getTotalCount(), unique.getCompletedUniques()));
                    break;
                default:
                    out.append("A different kind of Behavior\n");
            }
        }
    }
}