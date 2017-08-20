homebase
========

Uses Bearded Hen's Android Implementation of Twitter Bootstrap: https://github.com/Bearded-Hen/Android-Bootstrap

Check out his wiki for use information: https://github.com/Bearded-Hen/Android-Bootstrap/wiki

========

When importing projects (I used Eclipse - I dunno if Studio is different) ask to importing existing Android code and have it search for projects in the "root" directory - both should show up, import both.

Work should be done in homeBase.

Currently homeBase holds the Bootstrap example code, which is a bunch of xml files and MainActivity.java - keep the xml files around for now, but feel free to obviously rip apart the activity, it is only there for reference right now.

Get familiar with how forks work (https://help.github.com/articles/fork-a-repo) and pull requests work (https://help.github.com/articles/using-pull-requests).

You'll want to fork the project from my repo then, clone your fork to your machine, create an upstream that points to the master branch in the main repo you forked from, and probably create a branch on your fork for whatever issue you are currently tackling.  Then, you commit to your local working branch, push it to your fork, pull from upstream to remerge and fix any inconsistencies that don't auto-merge, THEN you commit->push again to your fork's working branch and finally create a pull request.  

Once you create a pull request, any further commits you push to the branch you are working on in your fork get added to that pull request - that's why if you do one pull request in an area, then want to work on another feature while you wait for the main repo to pull it in, you should be creating two branches.  Reading the forking/pull request articles will explain this better.

