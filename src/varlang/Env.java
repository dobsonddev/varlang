package varlang;
import java.util.List;

/**
 * Representation of an environment, which maps variables to values.
 * 
 * @author hridesh
 *
 */
public interface Env {
	boolean isEmpty();
	boolean hasBinding(String var);
	Value get (String search_var);

	@SuppressWarnings("serial")
	static public class LookupException extends RuntimeException {
		LookupException(String message){
			super(message);
		}
	}

	static public class EmptyEnv implements Env {
		public boolean isEmpty() {
			return true;
		}
		public boolean hasBinding(String var) {
			return false;
		}
		public Value get (String search_var) {
			throw new LookupException("No binding found for name: " + search_var);
		}
	}

	static public class ExtendEnv implements Env {
		public boolean isEmpty() {
			return false;
		}
		public boolean hasBinding(String var) {
			return _var.equals(var) || _saved_env.hasBinding(var);
		}
		private Env _saved_env; 
		private String _var; 
		private Value _val; 
		public ExtendEnv(Env saved_env, String var, Value val){
			_saved_env = saved_env;
			_var = var;
			_val = val;
		}
		public Value get (String search_var) {
			if (search_var.equals(_var))
				return _val;
			return _saved_env.get(search_var);
		}
	}
	static public class ExtendEnvList implements Env {
		public boolean isEmpty() {
			return _vars.isEmpty() && _saved_env.isEmpty();
		}
		public boolean hasBinding(String var) {
			return _vars.contains(var) || _saved_env.hasBinding(var);
		}
		private Env _saved_env; 
		private List<String> _vars; 
		private List<Value> _vals; 

		public ExtendEnvList(Env saved_env, List<String> vars, List<Value> vals){
			_saved_env = saved_env;
			_vars = vars;
			_vals = vals;
		}

		public Value get (String search_var) {
			for (int i = 0; i < _vars.size(); i++) {
				if (search_var.equals(_vars.get(i)))
					return _vals.get(i);
			}
			return _saved_env.get(search_var);
		}
	}
}
