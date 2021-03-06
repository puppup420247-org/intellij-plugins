// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.grazie.ide.language.comment

import com.intellij.grazie.grammar.strategy.GrammarCheckingStrategy
import com.intellij.grazie.grammar.strategy.StrategyUtils
import com.intellij.grazie.grammar.strategy.impl.ReplaceCharRule
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl

class CommentGrammarCheckingStrategy : GrammarCheckingStrategy {
  override fun isMyContextRoot(element: PsiElement) = element is PsiCommentImpl

  override fun getReplaceCharRules(root: PsiElement) = emptyList<ReplaceCharRule>()

  override fun getStealthyRanges(root: PsiElement, text: CharSequence) = StrategyUtils.indentIndexes(text, setOf(' ', '*', '/', '#'))
}
