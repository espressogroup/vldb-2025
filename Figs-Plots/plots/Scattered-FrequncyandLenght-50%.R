library(ggplot2)
library(scales)



df_new <- data.frame(
  T_Freq = c(
    330665, 330665, 330665, 223083, 223083, 223083, 119000, 119000, 119000,
    94790, 94790, 94790, 77397, 77397, 77397, 68182, 68182, 68182, 54885,
    54885, 54885, 36195, 36195, 36195, 38446, 38446, 38446, 3323, 3323, 3323,
    383804, 383804, 383804, 299486, 299486, 299486, 284156, 284156, 284156,
    239924, 239924, 239924, 214839, 214839, 214839, 119701, 119701, 119701,
    56858, 56858, 56858, 36709, 36709, 36709, 36684, 36684, 36684, 150, 150, 150,
    538725, 538725, 538725, 503585, 503585, 503585, 484838, 484838, 484838,
    443380, 443380, 443380, 417501, 417501, 417501, 412807, 412807, 412807,
    304039, 304039, 304039, 89908, 89908, 89908, 70083, 70083, 70083, 344, 344, 344
  ),
  TotalTime = c(
    474987.99, 472946.42, 407970.38, 279222.87, 289882.62, 283525.51,
    140205.01, 140787.77, 133671.61, 633187.83, 538108.07, 556300.54,
    611537.65, 567569.26, 560866.3, 139473.35, 147507.61, 131775.28,
    148261.56, 133474.82, 134153.51, 119056.9, 110482.02, 107876.5,
    158068.34, 128125.2, 128099.47, 51981.2, 51405.89, 49599.36,
    470392.9, 468417.38, 487389.62, 278728.37, 277628.07, 278935.84,
    402288.58, 280311.11, 334801.28, 348159.02, 345767.34, 346720.48,
    219804.75, 222227.14, 226933.78, 124243.65, 127134.52, 120895.75,
    115354.46, 117190.99, 114635.46, 111732.44, 112742.71, 110907.21,
    69426.21, 70626.33, 72285.31, 51981.2, 51405.89, 49599.36,
    1038101.6, 874731.12, 811491.08, 695110.6, 704800.75, 896651.13,
    638769.91, 606406.66, 608655.61, 621274.17, 623870.8, 625614.52,
    304226.17, 311506.22, 313250.04, 423655.01, 427241.59, 432069.85,
    423010.69, 419814.48, 420419.26, 688162.2, 581973.32, 578655.63,
    106416.38, 104528.6, 107734.23, 52048.59, 39874.32, 44376.94
  ),
  Keyword = c(
    rep("childhood inhaler", 3),
    rep("thanh759 memorial", 3),
    rep("milford regional", 3),
    rep("behavioral shields502", 3),
    rep("commonwealth neck", 3),
    rep("franecki195 eleni953", 3),
    rep("noelle559", 3),
    rep("vicente970", 3),
    rep("will178 bartell116", 3),
    rep("sheryl275 35969", 3),
    rep("corporation beth haywood675 faculty harvard", 3),
    rep("kuphal363 dickinson cleveland582 shriners davis923", 3),
    rep("weber641 sturdy memorial wynell591", 3),
    rep("tewksbury lowell child", 3),
    rep("brockton schmeler639 sallie654 christiansen251", 3),
    rep("falmouth miller503 dominic463 nikolaus26 isiah14", 3),
    rep("nantucket nicolas769 danial835 gussie514", 3),
    rep("solutions bartell116 orville751", 3),
    rep("cottage nantucket bernie827 mosciski958 gussie514", 3),
    rep("50603 deena887 cyndy549 35969", 3),
    rep("services england associates mickey576 gerlach374 baptist buckridge80", 3),
    rep("internal associates satterfield305 hintz995 eichmann909 waldo53", 3),
    rep("physicians corporation associated strosin214 beth faculty", 3),
    rep("new associates hilpert278 mickey576 baptist buckridge80", 3),
    rep("kuphal363 dickinson cooley cleveland582 shriners davis923 wolff180", 3),
    rep("practice valdés907 manuela585 public commission whitley", 3),
    rep("bashirian201 henry heywood rayford811 derick144 associates", 3),
    rep("simonne139 commonwealth neck yulanda554 elara upton", 3),
    rep("borer986 cottage bernie827 danial835 outreach gussie514", 3),
    rep("50603 mirna233 deena887 cyndy549 kasandra729 acosta403", 3)
  )
)



# Assuming the data is stored in a data frame called `df`
# Calculate QueryLength (number of words in Keyword)
df$QueryLength <- sapply(strsplit(df$Keyword, " "), length)

# Calculate average response times and query lengths for each T_Freq
avg_data <- aggregate(cbind(TotalTime, QueryLength) ~ T_Freq, data = df, FUN = mean)

# Plotting
ggplot(avg_data, aes(x = T_Freq, y = TotalTime)) +
  geom_point(aes(size = QueryLength, color = T_Freq), alpha = 0.8) +
  scale_x_log10(labels = comma) +
  scale_y_log10(labels = comma) +
  scale_color_gradient(low = "blue", high = "red") +
  labs(
    x = "Query Frequency Distribution (Log Scale)",
    y = "Average Response Time (ms, Log Scale)",
    size = "Query Length",
    color = "Frequency"
  ) +
  theme_minimal() +
  theme(
    plot.title = element_text(size = 25, face = "bold", hjust = 0.5),
    axis.title = element_text(size = 20, face = "bold"),
    axis.text = element_text(size = 20),
    legend.title = element_text(size = 20),
    legend.text = element_text(size = 20),
    legend.position = c(0, 1),  # Move the legend to the top-left corner
    legend.justification = c(0, 1),  # Align the legend box at the top-left
    panel.border = element_rect(color = "black", fill = NA, size = 1),
    plot.margin = margin(10, 10, 10, 10),
    aspect.ratio = 0.85
  ) +
  guides(
    size = guide_legend(title.position = "top"),
    color = guide_colorbar(title.position = "top")
  )