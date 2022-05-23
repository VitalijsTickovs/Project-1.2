file_name = "HillClimbing (ParticleSwarm) (terrain 1) (final-closest)-105074414320500.csv";

disp(mfilename('fullpath'));

path = erase(mfilename('fullpath'), "bot_whisker_plot");
path = erase(path, "plot");
path = path + "\bot\results\";

M = readmatrix(path+file_name);

tiledlayout(2,1);
%plt1 = nexttile;
%boxplot(M(2:end,1)); 
%title(plt1, 'Iterations')
plt2 = nexttile;
boxplot(M(2:end,2));
title(plt2, 'Simulations')
plt3 = nexttile;
boxplot(M(2:end,3)); 
title(plt3, 'Distance from target')