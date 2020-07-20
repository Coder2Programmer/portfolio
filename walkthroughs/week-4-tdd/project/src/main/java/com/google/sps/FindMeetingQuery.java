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

import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    int duration = (int)request.getDuration();
    Collection<TimeRange> availableTimeSlots = timeSlots(events, attendees);
    
    return availableTimeSlots.stream().filter(slot -> slot.duration() >= duration).collect(Collectors.toList());
  }

  private Collection<TimeRange> timeSlots(Collection<Event> events, Collection<String> attendees) {
    List<TimeRange> slots = new ArrayList<>();
    for (Event event : events) {
      if (existsAttendees(event, attendees)) {
        slots.add(event.getWhen());
      }
    }
    Collections.sort(slots, TimeRange.ORDER_BY_START);

    Collection<TimeRange> availableTimeSlots = new ArrayList<>();
    int start = TimeRange.START_OF_DAY;
    for (TimeRange slot : slots) {
      if (start < slot.start()) {
        availableTimeSlots.add(TimeRange.fromStartEnd(start, slot.start(), false));
      }
      start = Math.max(start, slot.end());
    }
    if (start < TimeRange.END_OF_DAY) {
      availableTimeSlots.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
    }
    return availableTimeSlots;
  }

  private boolean existsAttendees(Event event, Collection<String> attendees) {
    Set<String> eventAttendees = event.getAttendees();
    for (String attendee : eventAttendees) {
        if (attendees.contains(attendee)) {
            return true;
        }
    }
    return false;
  }
}
