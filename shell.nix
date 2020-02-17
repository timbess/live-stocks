with import <nixpkgs> {};

stdenv.mkDerivation {
  name = "purescript-bootstrap-shell";
  buildInputs = with pkgs; [
    nodejs
    yarn
  ];
}
