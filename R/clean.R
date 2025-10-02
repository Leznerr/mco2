filter_window <- function(df, years = 2021:2023) {
  df[df$FundingYear %in% years, , drop = FALSE]
}
