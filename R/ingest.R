#' Ingest a CSV file containing DPWH flood control projects.
#'
#' @param path Path to the CSV file to ingest.
#'
#' @return A tibble/data.frame with an attached "parse_issues" attribute that
#'   contains any parsing issues reported by readr. Emits a message summarizing
#'   parsing issues, if any.
ingest_csv <- function(path) {
  if (!requireNamespace("readr", quietly = TRUE)) {
    stop("Package 'readr' is required but not installed.", call. = FALSE)
  }

  if (is.null(path) || !nzchar(path)) {
    stop("A non-empty path must be provided.", call. = FALSE)
  }

  if (!file.exists(path)) {
    stop(sprintf("File not found: %s", path), call. = FALSE)
  }

  file_info <- file.info(path)
  if (is.na(file_info$size) || file_info$size <= 0) {
    stop(sprintf("File is empty: %s", path), call. = FALSE)
  }

  data <- readr::read_csv(
    file = path,
    locale = readr::locale(encoding = "UTF-8"),
    show_col_types = FALSE,
    progress = FALSE
  )

  problems <- readr::problems(data)
  attr(data, "parse_issues") <- problems

  if (nrow(problems) > 0) {
    message("parse_issues:")
    print(problems)
  } else {
    message("parse_issues: none")
  }

  data
}
