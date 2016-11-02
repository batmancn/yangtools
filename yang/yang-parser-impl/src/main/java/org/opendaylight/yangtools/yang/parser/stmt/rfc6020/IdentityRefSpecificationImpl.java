/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.parser.stmt.rfc6020;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.Rfc6020Mapping;
import org.opendaylight.yangtools.yang.model.api.meta.EffectiveStatement;
import org.opendaylight.yangtools.yang.model.api.stmt.BaseStatement;
import org.opendaylight.yangtools.yang.model.api.stmt.IdentityStatement;
import org.opendaylight.yangtools.yang.model.api.stmt.TypeStatement;
import org.opendaylight.yangtools.yang.parser.spi.IdentityNamespace;
import org.opendaylight.yangtools.yang.parser.spi.SubstatementValidator;
import org.opendaylight.yangtools.yang.parser.spi.meta.AbstractDeclaredStatement;
import org.opendaylight.yangtools.yang.parser.spi.meta.AbstractStatementSupport;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContext;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContextUtils;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.effective.type.IdentityRefSpecificationEffectiveStatementImpl;

public class IdentityRefSpecificationImpl extends AbstractDeclaredStatement<String> implements TypeStatement.IdentityRefSpecification {
    private static final SubstatementValidator SUBSTATEMENT_VALIDATOR = SubstatementValidator.builder(Rfc6020Mapping
            .TYPE)
            .addMandatory(Rfc6020Mapping.BASE)
            .build();

    protected IdentityRefSpecificationImpl(
            final StmtContext<String, TypeStatement.IdentityRefSpecification, ?> context) {
        super(context);
    }

    public static class Definition
            extends
            AbstractStatementSupport<String, TypeStatement.IdentityRefSpecification, EffectiveStatement<String, TypeStatement.IdentityRefSpecification>> {

        public Definition() {
            super(Rfc6020Mapping.TYPE);
        }

        @Override
        public String parseArgumentValue(final StmtContext<?, ?, ?> ctx, final String value) {
            return value;
        }

        @Override
        public TypeStatement.IdentityRefSpecification createDeclared(
                final StmtContext<String, TypeStatement.IdentityRefSpecification, ?> ctx) {
            return new IdentityRefSpecificationImpl(ctx);
        }

        @Override
        public EffectiveStatement<String, TypeStatement.IdentityRefSpecification> createEffective(
                final StmtContext<String, TypeStatement.IdentityRefSpecification, EffectiveStatement<String, TypeStatement
                        .IdentityRefSpecification>> ctx) {
            return new IdentityRefSpecificationEffectiveStatementImpl(ctx);
        }

        @Override
        public void onFullDefinitionDeclared(final StmtContext.Mutable<String, IdentityRefSpecification,
                EffectiveStatement<String, IdentityRefSpecification>> stmt) {
            final StmtContext<QName, ?, ?> baseStmt = StmtContextUtils.findFirstDeclaredSubstatement(stmt,
                    BaseStatement.class);
            Preconditions.checkArgument(baseStmt != null, "The \"base\" statement, which is a substatement to the " +
                    "\"type\"\n statement, MUST be present if the type is \"identityref\" in source '%s'", stmt
                    .getStatementSourceReference());
            final QName baseIdentity = baseStmt.getStatementArgument();
            final StmtContext<?, IdentityStatement, EffectiveStatement<QName, IdentityStatement>> stmtCtx = stmt
                    .getFromNamespace(IdentityNamespace.class, baseIdentity);
            Preconditions.checkArgument(stmtCtx != null, "Referenced base identity '%s' doesn't exist " +
                        "in " + "given scope " + "(module, imported submodules), source: '%s'", baseIdentity
                    .getLocalName(), stmt.getStatementSourceReference());
        }

        @Override
        public void onStatementDefinitionDeclared(final StmtContext.Mutable<String, IdentityRefSpecification,
                EffectiveStatement<String, IdentityRefSpecification>> stmt) {
            super.onStatementDefinitionDeclared(stmt);
            SUBSTATEMENT_VALIDATOR.validate(stmt);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return argument();
    }

    @Nonnull
    @Override
    public BaseStatement getBase() {
        return firstDeclared(BaseStatement.class);
    }

}
