package daniil.dobris.notes.controllers;

import daniil.dobris.notes.entities.Note;
import daniil.dobris.notes.entities.User;
import daniil.dobris.notes.service.NoteService;
import daniil.dobris.notes.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;
    private final UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    public void checkAccessToNote(Long userId, UserDetails userDetails) {
        Optional<User> curUser = userService.findUserByUsername(userDetails.getUsername());
        if (curUser.isPresent()) {
            if (!curUser.get().getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Access Denied! You cannot access other users' notes.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
        }
    }
    @GetMapping
    public String showNotesPage(@RequestParam("userId") Long userId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        checkAccessToNote(userId, userDetails);
        List<Note> notes = noteService.getNotesByUserId(userId);
        User curUser = userService.findUserById(userId);
        model.addAttribute("user", curUser);
        model.addAttribute("notes", notes);
        model.addAttribute("userId", userId);
        return "notes";
    }
    @GetMapping("/create")
    public String showCreateNotePage(@RequestParam("userId") Long userId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        checkAccessToNote(userId, userDetails);
        User user = userService.findUserById(userId);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "login";
        }
        model.addAttribute("user", user);
        return "create_note";
    }
    @PostMapping("/create")
    public String createNote(@RequestParam("userId") Long userId,
                             @RequestParam("title") String title, @RequestParam("content") String content,
                             @AuthenticationPrincipal UserDetails userDetails, Model model) {
        checkAccessToNote(userId, userDetails);
        if (!noteService.createNote(userId, title, content)) {
            model.addAttribute("message", "This user is not exist");
            return "redirect:/notes";
        }
        return "redirect:/notes?userId=" + userId;
    }
    @GetMapping("/edit/{id}")
    public String showEditNoteForm(@PathVariable("id") Long id,
                                   @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Note note = noteService.getNoteById(id);
        if (note != null) {
            checkAccessToNote(note.getUser().getId(), userDetails);
            model.addAttribute("note", note);
            model.addAttribute("userId", note.getUser().getId());
            return "edit_note";
        } else {
            model.addAttribute("error", "Note has not found");
            return "redirect:/notes";
        }
    }
    @PostMapping("/edit/{id}")
    public String editNote(@PathVariable("id") Long id,
                           @RequestParam("title") String title, @RequestParam("content") String content,
                           @RequestParam("userId") Long userId, @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        checkAccessToNote(userId, userDetails);
        if (content == null || content.trim().isEmpty()) {
            noteService.deleteNote(id);
            return "redirect:/notes?userId=" + userId;
        }
        if (noteService.updateNote(id, title, content)) {
            return "redirect:/notes?userId=" + userId;
        } else {
            model.addAttribute("error", "This note has not found");
            return "redirect:/notes/edit/{" + id + "}";
        }
    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String deleteNode(@PathVariable("id") Long id, @RequestParam("userId") Long userId,
                             @AuthenticationPrincipal UserDetails userDetails) {
        checkAccessToNote(userId, userDetails);
        noteService.deleteNote(id);
        return "redirect:/notes?userId=" + userId;
    }
}
