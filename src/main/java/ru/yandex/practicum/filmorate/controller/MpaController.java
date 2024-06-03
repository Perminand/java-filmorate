package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;


@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController implements IntefaceController<Mpa> {
    private final MpaService mpaService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Mpa> getAll() {
        return mpaService.getAll();
    }

    @Override
    @GetMapping("/{mpa_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mpa getById(@PathVariable("mpa_id") final Long id) {
        return mpaService.getById(id);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mpa create(@Valid @RequestBody final Mpa data) {
        return mpaService.create(data);
    }

    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Mpa update(@Valid @RequestBody final Mpa data) {
        return mpaService.update(data);
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void delete() {
        mpaService.delete();
    }

    @Override
    @DeleteMapping("/{Mpa_id}")
    public void deleteById(@Valid @PathVariable("Mpa_id") Long id) {
        mpaService.deleteById(id);
    }
}

