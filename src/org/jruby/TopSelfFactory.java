/***** BEGIN LICENSE BLOCK *****
 * Version: EPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Eclipse Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/epl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the EPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the EPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby;

import org.jruby.runtime.Arity;
import org.jruby.runtime.Block;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.callback.Callback;

/**
 * 
 * @author jpetersen
 */
public final class TopSelfFactory {

    /**
     * Constructor for TopSelfFactory.
     */
    private TopSelfFactory() {
        super();
    }
    
    public static IRubyObject createTopSelf(final Ruby runtime) {
        IRubyObject topSelf = new RubyObject(runtime, runtime.getObject());
        
        topSelf.getSingletonClass().defineFastMethod("to_s", new Callback() {
            /**
             * @see org.jruby.runtime.callback.Callback#execute(IRubyObject, IRubyObject[])
             */
            public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                return runtime.newString("main");
            }

            /**
             * @see org.jruby.runtime.callback.Callback#getArity()
             */
            public Arity getArity() {
                return Arity.noArguments();
            }
        });
        
        // The following three methods must be defined fast, since they expect to modify the current frame
        // (i.e. they expect no frame will be allocated for them). JRUBY-1185.
        topSelf.getSingletonClass().defineFastPrivateMethod("include", new Callback() {
            /**
             * @see org.jruby.runtime.callback.Callback#execute(IRubyObject, IRubyObject[])
             */
            public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                return runtime.getObject().include(args);
            }

            /**
             * @see org.jruby.runtime.callback.Callback#getArity()
             */
            public Arity getArity() {
                return Arity.optional();
            }
        });
        
        topSelf.getSingletonClass().defineFastPrivateMethod("public", new Callback() {
            /**
             * @see org.jruby.runtime.callback.Callback#execute(IRubyObject, IRubyObject[])
             */
            public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
                return runtime.getObject().rbPublic(recv.getRuntime().getCurrentContext(), args);
            }

            /**
             * @see org.jruby.runtime.callback.Callback#getArity()
             */
            public Arity getArity() {
                return Arity.optional();
            }
        });
        
        topSelf.getSingletonClass().defineFastPrivateMethod("private", new Callback() {
            /**
             * @see org.jruby.runtime.callback.Callback#execute(IRubyObject, IRubyObject[])
             */
            public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
                return runtime.getObject().rbPrivate(recv.getRuntime().getCurrentContext(), args);
            }

            /**
             * @see org.jruby.runtime.callback.Callback#getArity()
             */
            public Arity getArity() {
                return Arity.optional();
            }
        });
        
        return topSelf;
    }
}
