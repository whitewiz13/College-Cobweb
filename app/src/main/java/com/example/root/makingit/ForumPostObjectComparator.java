package com.example.root.makingit;

import java.util.Comparator;

public class ForumPostObjectComparator implements Comparator<ForumPostInfo>  {

    @Override
    public int compare(ForumPostInfo forumPost1, ForumPostInfo forumPost2) {
        return forumPost1.getFname().compareTo(forumPost2.getFname());
    }
}