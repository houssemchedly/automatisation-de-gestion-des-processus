package idvey.testapi.meet;

import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("meetings")
@RequiredArgsConstructor
@Tag(name = "Meeting")
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    @Operation(summary = "Create a new online meeting")
    public ResponseEntity<Integer> saveMeeting(
            @Valid @RequestBody MeetingRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(meetingService.save(request, connectedUser));
    }

    @GetMapping("/{meeting-id}")
    @Operation(summary = "Get meeting by ID")
    public ResponseEntity<MeetingResponse> findMeetingById(
            @PathVariable("meeting-id") Integer meetingId
    ) {
        return ResponseEntity.ok(meetingService.findById(meetingId));
    }

    @GetMapping
    @Operation(summary = "Get all meetings with pagination")
    public ResponseEntity<PageResponse<MeetingResponse>> findAllMeetings(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(meetingService.findAllMeetings(page, size));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get meetings by type")
    public ResponseEntity<PageResponse<MeetingResponse>> findMeetingsByType(
            @PathVariable meetype type,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(meetingService.findMeetingsByType(type, page, size));
    }

    @GetMapping("/project/{project-id}")
    @Operation(summary = "Get meetings by project")
    public ResponseEntity<PageResponse<MeetingResponse>> findMeetingsByProject(
            @PathVariable("project-id") Integer projetId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(meetingService.findMeetingsByProject(projetId, page, size));
    }

    @GetMapping("/my-meetings")
    @Operation(summary = "Get current user's meetings")
    public ResponseEntity<PageResponse<MeetingResponse>> findMyMeetings(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(meetingService.findMyMeetings(connectedUser, page, size));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming meetings")
    public ResponseEntity<List<MeetingResponse>> findUpcomingMeetings() {
        return ResponseEntity.ok(meetingService.findUpcomingMeetings());
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's meetings")
    public ResponseEntity<List<MeetingResponse>> findTodaysMeetings() {
        return ResponseEntity.ok(meetingService.findTodaysMeetings());
    }

    @PutMapping("/{meeting-id}")
    @Operation(summary = "Update a meeting")
    public ResponseEntity<Integer> updateMeeting(
            @PathVariable("meeting-id") Integer meetingId,
            @Valid @RequestBody MeetingRequest request
    ) {
        return ResponseEntity.ok(meetingService.updateMeeting(meetingId, request));
    }

    @PostMapping("/{meeting-id}/participants/{user-id}")
    @Operation(summary = "Add a participant to a meeting")
    public ResponseEntity<Void> addParticipant(
            @PathVariable("meeting-id") Integer meetingId,
            @PathVariable("user-id") Integer userId
    ) {
        meetingService.addParticipant(meetingId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{meeting-id}/participants/{user-id}")
    @Operation(summary = "Remove a participant from a meeting")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable("meeting-id") Integer meetingId,
            @PathVariable("user-id") Integer userId
    ) {
        meetingService.removeParticipant(meetingId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{meeting-id}")
    @Operation(summary = "Delete a meeting")
    public ResponseEntity<Void> deleteMeeting(
            @PathVariable("meeting-id") Integer meetingId
    ) {
        meetingService.deleteMeeting(meetingId);
        return ResponseEntity.noContent().build();
    }
}
