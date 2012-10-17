package ru.android.hellslade.autochmo;

import android.content.SearchRecentSuggestionsProvider;

public class SuggestionProvider extends SearchRecentSuggestionsProvider {
  public final static String AUTHORITY = "ru.android.hellslade.autochmo.SuggestionProvider";
  public final static int MODE = DATABASE_MODE_QUERIES;

  public SuggestionProvider() {
    setupSuggestions(AUTHORITY, MODE);
  }
}