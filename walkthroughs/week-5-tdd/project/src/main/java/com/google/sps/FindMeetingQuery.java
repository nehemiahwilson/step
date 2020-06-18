// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
      if (events.equals(Collections.emptySet()) &&
            request.getDuration() <= TimeRange.WHOLE_DAY.duration()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
      } else if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
          return Arrays.asList();
      }
      long requestedMeetingDuration = request.getDuration();
      ArrayList<TimeRange> timeRanges = new ArrayList<>();
      ArrayList<TimeRange> possibleMeetingTimes = new ArrayList<>();
      for (Event e : events) {
        timeRanges.add(e.getWhen());
      }
      if (timeRanges.size() == 1) {
        // assuming one time range
        String eventAttendee = "";
        String requestAttendee = "";
        for (Event e : events) {
            for (String attendee : e.getAttendees()) {
                eventAttendee = attendee;
            }
        }
        for (String attendee : request.getAttendees()) {
            requestAttendee = attendee;
        }
        if (eventAttendee.equals(requestAttendee)) {
            TimeRange firstTimeRange = timeRanges.get(0);
            possibleMeetingTimes.add(TimeRange.fromStartDuration(0, firstTimeRange.start()));
            possibleMeetingTimes.add(TimeRange.fromStartDuration(firstTimeRange.end(), TimeRange.END_OF_DAY - firstTimeRange.end() + 1));
        } else {
            possibleMeetingTimes.add(TimeRange.fromStartDuration(0, TimeRange.WHOLE_DAY.duration()));
        }
      } else {
        // assuming two time ranges
        Collections.sort(timeRanges, TimeRange.ORDER_BY_START);
        TimeRange firstTimeRange = timeRanges.get(0);
        TimeRange secondTimeRange = timeRanges.get(1);
        int duration = 0;

        if (!firstTimeRange.overlaps(secondTimeRange)) {
            // not overlapping
            if (firstTimeRange.start() != 0) {
                duration = firstTimeRange.start();
                if (duration >= requestedMeetingDuration) {
                    possibleMeetingTimes.add(TimeRange.fromStartDuration(0, duration));
                }
            }
            duration = secondTimeRange.start() - firstTimeRange.end();
            if (duration >= requestedMeetingDuration) {
                possibleMeetingTimes.add(TimeRange.fromStartDuration(firstTimeRange.end(), duration));
            }
            duration = TimeRange.END_OF_DAY - secondTimeRange.end();
            if (duration >= requestedMeetingDuration) {
                possibleMeetingTimes.add(TimeRange.fromStartDuration(secondTimeRange.end(), duration + 1));
            }
        } else { // overlapping
            if (firstTimeRange.contains(secondTimeRange)) {
                duration = firstTimeRange.start();
                if (duration >= requestedMeetingDuration) {
                    possibleMeetingTimes.add(TimeRange.fromStartDuration(0, duration));
                }
                duration = TimeRange.END_OF_DAY - firstTimeRange.end() + 1;
                if (duration >= requestedMeetingDuration) {
                    possibleMeetingTimes.add(TimeRange.fromStartDuration(firstTimeRange.end(), duration));
                }
            } else if (secondTimeRange.contains(firstTimeRange)) {
                duration = secondTimeRange.start();
                if (duration >= requestedMeetingDuration) {
                    possibleMeetingTimes.add(TimeRange.fromStartDuration(0, duration));
                }
                duration = TimeRange.END_OF_DAY - secondTimeRange.end() + 1;
                if (duration >= requestedMeetingDuration) {
                    possibleMeetingTimes.add(TimeRange.fromStartDuration(secondTimeRange.end(), duration));
                }
            } else { // neither contains the other
                int startTime;
                int endTime;
                if (firstTimeRange.start() < secondTimeRange.start()) {
                    startTime = firstTimeRange.start();
                    endTime = secondTimeRange.end();
                } else {
                    startTime = secondTimeRange.start();
                    endTime = firstTimeRange.end();
                }
                duration = startTime;
                if (duration >= requestedMeetingDuration) {
                    possibleMeetingTimes.add(TimeRange.fromStartDuration(0, duration));
                }
                duration = TimeRange.END_OF_DAY - endTime + 1;
                if (duration >= requestedMeetingDuration) {
                    possibleMeetingTimes.add(TimeRange.fromStartDuration(endTime, duration));
                }
            }
        }
      }
    return possibleMeetingTimes;
  }
}
