library(ggplot2)
library(scales)


df <- data.frame(
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
    217919.51, 178565.66, 217919.51, 125182.13, 128852.38, 136373.06, 63864.65,
    67619.85, 64568.31, 273768.31, 282681.31, 252120, 283390.65, 302589.99,
    300418.25, 78765.17, 120329.85, 60382.89, 70304.77, 80453.11, 82832.75,
    56587.54, 57168.41, 55484.08, 66359.67, 68887.9, 70539.42, 37042.03, 34838.19,
    34703.03, 244795.19, 257722.56, 244095.42, 166106.29, 183160.89, 200909.92,
    146674.22, 139026.55, 145311.97, 169840.24, 170294.67, 175212.4, 102263.7,
    101381.37, 112937.53, 61683.93, 62159.05, 65749.39, 59246.46, 55229.49,
    55022.69, 46859.92, 47676.85, 46978.06, 35387.2, 37206.02, 39666.3, 12211,
    12211.45, 11968.14, 403353.47, 410312.03, 345412.37, 301486.78, 323425.23,
    301486.78, 265688.24, 269836.93, 251906.15, 262794.75, 266441.27, 265040.54,
    132239.25, 156346.39, 174406.55, 279435.26, 258006.01, 223251.79, 209479.33,
    231786.86, 241763.35, 273601.07, 307413.63, 358206.93, 49942.01, 48355.83,
    53134.45, 26576.65, 20026.44, 26576.65
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